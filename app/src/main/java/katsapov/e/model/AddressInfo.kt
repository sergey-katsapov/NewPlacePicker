package katsapov.e.model

class AddressInfo(uuid: String, tag: String, addressName: String, latitude: String, longitude: String) {

    var uuid: String? = null
    var tag: String? = null
    var addressName: String? = null
    var latitude: String? = null
    var longitude: String? = null

    init {
        this.uuid = uuid
        this.tag = tag
        this.addressName = addressName
        this.latitude = latitude
        this.longitude = longitude
    }
}
