package ru.landilf.hellofbullets.domain.model.skills

data class SkillProgress(
    val skillId: Long,
    val level: Int
) {
    init {
        require(level >= 1) {
            "Уровень разблокированного навыка должен быть положительным"
        }
    }
}