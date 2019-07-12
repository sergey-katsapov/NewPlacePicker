package katsapov.e.model

class AddressInfo(tag: String, addressName: String, latitude: String,longitude: String) {

    var tag: String? = null
    var addressName: String? = null
    var latitude: String? = null
    var longitude: String? = null

    init {
        this.tag = tag
        this.addressName = addressName
        this.latitude = latitude
        this.longitude = longitude
    }
}
