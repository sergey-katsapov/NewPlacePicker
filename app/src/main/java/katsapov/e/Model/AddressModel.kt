package katsapov.e.Model

class AddressModel(name: String, type: String, feature: String) {

    var name: String
        internal set
    var type: String
        internal set
    var feature: String
        internal set

    init {
        this.name = name
        this.type = type
        this.feature = feature
    }

}
