class Message(val type: MessageType, val from: Int, val to: Int, val level: Int) {

    override
    fun toString() : String {
        return "{  $from -> $to : $level }"
    }

}