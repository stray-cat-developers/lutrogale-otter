package io.mustelidae.otter.lutrogale.api.common

open class Location(
    var latitude: Double,
    var longitude: Double
) {
    /* 주소 */
    var address: String? = null
    /* 지명 */
    var name: String? = null
    /* 지역에 대한 상세 설명*/
    var description: String? = null
}
