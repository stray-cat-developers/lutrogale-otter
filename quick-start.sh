docker-compose up &

VERSION=$(./gradlew version -q)

./gradlew clean

if [ ! -f build/libs/lutrogale-"$VERSION".jar ]; then
	./gradlew build -x test
fi

java \
	-XX:MaxMetaspaceSize=200m \
	-Xmx1024m \
	-jar build/libs/lutrogale-"$VERSION".jar
