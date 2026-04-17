package com.example.tsumap.ui.screens.tree

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.tree.DecisionTreeLoader
import com.example.tsumap.data.tree.DecisionTreeParser
import com.example.tsumap.domain.usecase.tree.ClassificationResult
import com.example.tsumap.domain.usecase.tree.DecisionTreeUseCase
import com.example.tsumap.domain.usecase.tree.DiningAnswers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class QuestionStep(
    val attribute: String,
    val label: String,
    val options: List<Pair<String, String>>
)

private val STEPS = listOf(
    QuestionStep(
        attribute = "food_type",
        label = "Что хочешь?",
        options = listOf(
            "coffee" to "Кофе ☕",
            "full_meal" to "Полноценный обед 🍱",
            "pancakes" to "Блины 🥞",
            "snack" to "Перекус 🥪"
        )
    ),
    QuestionStep(
        attribute = "location",
        label = "Где ты находишься?",
        options = listOf(
            "bus_stop" to "Автобусная остановка 🚌",
            "campus_center" to "Центр кампуса 🏛️",
            "main_building" to "Главный корпус 📚",
            "second_building" to "Второй корпус 🏢"
        )
    ),
    QuestionStep(
        attribute = "budget",
        label = "Какой бюджет?",
        options = listOf(
            "low" to "Низкий 💸",
            "medium" to "Средний 💰",
            "high" to "Высокий 💎"
        )
    ),
    QuestionStep(
        attribute = "time_available",
        label = "Сколько времени?",
        options = listOf(
            "very_short" to "Совсем мало - до 15 мин ⚡",
            "short" to "Немного - 15-30 мин 🕐"
        )
    )
)

private val PLACE_NAMES = mapOf(
    "starbooks" to "StarBooks",
    "cafe_main" to "Столовая",
    "pancakes" to "Сибирские блины",
    "yarche" to "Ярче",
    "coffe1" to "Белка кофе",
    "coffe2" to "Пеки Лола",
    "our" to "Гастроном"
)

class DecisionTreeViewModel(private val context: Context) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep

    private val _answers = MutableStateFlow<Map<String, String>>(emptyMap())
    val answers: StateFlow<Map<String, String>> = _answers

    private val _result = MutableStateFlow<ClassificationResult?>(null)
    val result: StateFlow<ClassificationResult?> = _result

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val steps = STEPS
    val placeNames = PLACE_NAMES

    fun selectOption(value: String) {
        val step = STEPS[_currentStep.value]
        val newAnswers = _answers.value.toMutableMap()
        newAnswers[step.attribute] = value
        _answers.value = newAnswers

        if (_currentStep.value < STEPS.size - 1) {
            _currentStep.value++
        } else {
            classify(newAnswers)
        }
    }

    fun back() {
        if (_currentStep.value > 0) {
            _currentStep.value--
            _result.value = null
        }
    }

    fun reset() {
        _currentStep.value = 0
        _answers.value = emptyMap()
        _result.value = null
    }

    private fun classify(answers: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val json = DecisionTreeLoader.load(context)
            val tree = DecisionTreeParser.parse(json)
            val useCase = DecisionTreeUseCase(tree)
            val diningAnswers = DiningAnswers(
                location = answers["location"] ?: "",
                budget = answers["budget"] ?: "",
                timeAvailable = answers["time_available"] ?: "",
                foodType = answers["food_type"] ?: "",
                queueTolerance = "medium",
                weather = "any"
            )
            _result.value = useCase.invoke(diningAnswers)
            _isLoading.value = false
        }
    }
}
