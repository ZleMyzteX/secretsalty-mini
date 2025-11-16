package er.codes.secretsaltyminimal.dto

import er.codes.secretsaltyminimal.jooq.tables.pojos.Users

fun Users.toUserDto(): UserDto {
    return UserDto(
        username = this.username ?: throw IllegalStateException("Username cannot be null"),
        userId = this.userId ?: throw IllegalStateException("UserId cannot be null")
    )
}

fun Users.toUserWithWishDto(): UserWithWishDto {
    return UserWithWishDto(
        username = this.username ?: throw IllegalStateException("Username cannot be null"),
        wishes = this.wishes ?: ""
    )
}