import os
import anthropic
import requests
import json

# 환경 변수 가져오기
github_token = os.environ["GITHUB_TOKEN"]
anthropic_api_key = os.environ["ANTHROPIC_API_KEY"]
pr_number = os.environ["PR_NUMBER"]
repo_name = os.environ["REPO_NAME"]

# API 클라이언트 초기화
client = anthropic.Anthropic(api_key=anthropic_api_key)

# GitHub API 기본 URL
api_url = f"https://api.github.com/repos/{repo_name}"

# PR의 변경 파일 가져오기
headers = {
    "Authorization": f"token {github_token}",
    "Accept": "application/vnd.github.v3+json"
}

print(f"PR 번호: {pr_number}")
print(f"저장소명: {repo_name}")

try:
    files_url = f"{api_url}/pulls/{pr_number}/files"
    files_response = requests.get(files_url, headers=headers)

    if files_response.status_code != 200:
        print(f"GitHub API 오류: {files_response.status_code} - {files_response.text}")
        exit(1)

    # 응답을 JSON으로 파싱
    files = files_response.json()

    # Diff 내용 수집
    diff_content = ""
    if isinstance(files, list):
        for file in files:
            if isinstance(file, dict):
                filename = file.get('filename', 'Unknown')
                patch = file.get('patch', '파일 내용 없음')
                diff_content += f"File: {filename}\n{patch}\n\n"

        print(f"총 {len(files)}개 파일의 변경사항을 수집했습니다.")
    else:
        print("오류: 파일 목록이 리스트 형식이 아닙니다.")
        exit(1)

    if not diff_content:
        print("변경된 내용이 없거나 패치 정보를 가져올 수 없습니다.")
        exit(0)

    print("코드 리뷰 요청을 Claude API에 전송 중...")

    # PR 제목과 설명 가져오기
    pr_url = f"{api_url}/pulls/{pr_number}"
    pr_response = requests.get(pr_url, headers=headers)
    pr_data = pr_response.json()
    pr_title = pr_data.get('title', 'No title')
    pr_description = pr_data.get('body', 'No description')

    # Claude API 호출
    # 외부 파일에서 프롬프트 템플릿 로드
    prompt_file_path = os.path.join(os.path.dirname(__file__), 'prompt.txt')
    try:
        with open(prompt_file_path, 'r', encoding='utf-8') as file:
            prompt_template = file.read()
        print(f"프롬프트 템플릿을 '{prompt_file_path}'에서 성공적으로 로드했습니다.")
    except Exception as e:
        print(f"프롬프트 파일 로드 오류: {str(e)}")
        exit(1)

    # format() 메서드로 값 채우기
    prompt = prompt_template.format(
        pr_number=pr_number,
        pr_title=pr_title,
        pr_description=pr_description,
        diff_content=diff_content
    )

    response = client.messages.create(
        model="claude-sonnet-4-5-20250929",
        max_tokens=15000,
        messages=[
            {"role": "user", "content": prompt}
        ]
    )

    review_comment = response.content[0].text

    print("Claude의 코드 리뷰 완료, PR에 코멘트 게시 중...")

    # PR에 리뷰 코멘트 게시
    comment_data = {
        "body": f"## Claude AI 코드 리뷰\n\n{review_comment}"
    }

    comment_url = f"{api_url}/issues/{pr_number}/comments"
    comment_response = requests.post(
        comment_url,
        headers=headers,
        json=comment_data
    )

    if comment_response.status_code in [201, 200]:
        print("코드 리뷰가 PR에 성공적으로 게시되었습니다.")
    else:
        print(f"코멘트 게시 실패: {comment_response.status_code} - {comment_response.text}")

except Exception as e:
    print(f"오류 발생: {str(e)}")
    exit(1)