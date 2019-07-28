package com.example.validator

import android.view.View
import java.lang.ref.WeakReference

@DslMarker
annotation class ValidatorDsl

class Validator {

    private var rules: Map<WeakReference<View>, List<Rule<View>>>? = null

    operator fun invoke(@ValidatorDsl action: ValidatorConfigurator.() -> Unit) {
        val configurator = ValidatorConfigurator().apply(action)
        rules = configurator.rules
        rules?.forEach { entry ->
            val view = entry.key.get()
            if (view != null) {
                entry.value.forEach { rule ->
                    rule.init(view)
                }
            }
        }
    }

    fun validate(): Boolean {
        var valid = true
        rules?.forEach { entry ->
            val view = entry.key.get()
            if (view != null) {
                entry.value.forEach { rule ->
                    val invalidRules = mutableListOf<Rule<View>>()
                    if (!rule.isValid(view)) {
                        invalidRules.add(rule)
                    } else {
                        rule.onValid(view)
                    }
                    if (invalidRules.size > 0) {
                        valid = false
                        showPriorityError(view, invalidRules)
                    }
                }
            }
        }
        return valid
    }

    private fun showPriorityError(view: View, invalidRules: List<Rule<View>>) {
        val highestPriority = requireNotNull(invalidRules.map { it.priority }.max())
        println()
        invalidRules.filter { it.priority == highestPriority }.forEach {
            it.onInvalid(view)
        }
    }
}

@ValidatorDsl
class ValidatorConfigurator {

    private val _rules = mutableMapOf<WeakReference<View>, List<Rule<View>>>()
    val rules: Map<WeakReference<View>, List<Rule<View>>> = _rules

    fun <V : View> on(view: V, action: RulesBuilder<V>.() -> Unit) {
        val builder = RulesBuilder<V>().apply(action)
        @Suppress("UNCHECKED_CAST")
        _rules[WeakReference(view as View)] = builder.rules as List<Rule<View>>
    }
}

@ValidatorDsl
class RulesBuilder<V : View> {
    private val _rules = mutableListOf<Rule<V>>()
    val rules: List<Rule<V>> = _rules

    fun add(rule: Rule<V>) {
        _rules.add(rule)
    }
}


