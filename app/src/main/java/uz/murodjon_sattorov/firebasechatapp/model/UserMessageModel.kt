package uz.murodjon_sattorov.firebasechatapp.model

import java.io.Serializable


/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/15/2021
 * @project Firebase Chat app
 */
class UserMessageModel: Serializable {

    var sender: String? = null
    var receiver: String? = null
    var messageImageLink: String? = null
    var message: String? = null
    var messageDate: String? = null
    var messageRead: Boolean? = null

    constructor(){}

    constructor(
        sender: String,
        receiver: String,
        messageImageLink: String,
        message: String,
        messageDate: String,
        messageRead: Boolean
    ){
        this.sender = sender
        this.receiver = receiver
        this.messageImageLink = messageImageLink
        this.message = message
        this.messageDate = messageDate
        this.messageRead = messageRead
    }


}