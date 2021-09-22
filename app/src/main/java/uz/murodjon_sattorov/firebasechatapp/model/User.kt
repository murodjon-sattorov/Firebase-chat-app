package uz.murodjon_sattorov.firebasechatapp.model

import java.io.Serializable

/**
 * Created by <a href="mailto: sattorovmurodjon43@gmail.com">Murodjon Sattorov</a>
 *
 * @author Murodjon
 * @date 9/7/2021
 * @project Firebase Chat app
 */
class User: Serializable {
    var id: String? = null
    var userFullName: String? = null
    var userName: String? = null
    var userImgURL: String? = null
    var bio: String? = null
    var status: String? = null
    var userPassword: String? = null
    var userPhoneNumber: String? = null

    constructor() {}

    constructor(
        id: String?,
        userFullName: String?,
        userName: String?,
        userImgURL: String?,
        bio: String?,
        status: String?,
        userPassword: String?,
        userPhoneNumber: String?
    ) {
        this.id = id
        this.userFullName = userFullName
        this.userName = userName
        this.userImgURL = userImgURL
        this.bio = bio
        this.status = status
        this.userPassword = userPassword
        this.userPhoneNumber = userPhoneNumber
    }
}
