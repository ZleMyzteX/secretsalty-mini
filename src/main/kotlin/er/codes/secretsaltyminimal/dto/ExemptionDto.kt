package er.codes.secretsaltyminimal.dto

data class ExemptionDto(
    val fromUser: UserDto,
    val toUser: UserDto
)

data class ExemptionRequest(val excludedUserId: String)