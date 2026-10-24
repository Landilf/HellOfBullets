package ru.landilf.hellofbullets.domain.model.skills.definition

data class TimeSlowLevelParameters(
    val activeParameters: ActiveSkillLevelParameters,
    val slowdownPercent: Float
) {
    init {
        require(slowdownPercent > 0f && slowdownPercent < 100f) {
            "Замедление должно быть больше 0% и меньше 100%"
        }
    }
}
