package account.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ChangePassData @JsonCreator public constructor(@JsonProperty("new_password") var new_password: String) {

}