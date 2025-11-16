package er.codes.secretsaltyminimal.dto

data class UserDto(val userId: String, val username: String)

data class UserWithWishDto(val username: String, val wishes: String)

data class WishDto(val wish: String)

data class ChangeUsernameDto(val username: String)