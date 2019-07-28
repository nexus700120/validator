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
        rules?.forEach rules@{ entry ->
            val view = entry.key.get()
            if (view != null) {
                entry.value.forEach { rule ->
                    if (!rule.isValid(view)) {
                        rule.onInvalid(view)
                        valid = false
                        return@rules
                    } else {
                        rule.onValid(view)
                    }
                }
            }
        }
        return valid
    }
}

@ValidatorDsl
class ValidatorConfigurator {

    private val _rules = mutableMapOf<WeakReference<View>, List<Rule<View>>>()
    val rules: Map<WeakReference<View>, List<Rule<View>>> = _rules

    fun <V : View> on(view: V, action: RulesBuilder<V>.() -> Unit) {
        val builder = RulesBuilder<V>().apply(action)
        _rules[WeakReference(view as View)] = builder.rules
    }
}

@ValidatorDsl
class RulesBuilder<V : View> {
    private val _rules = mutableListOf<Rule<View>>()
    val rules: List<Rule<View>> = _rules

    fun add(rule: Rule<V>) {
        @Suppress("UNCHECKED_CAST")
        _rules.add(rule as Rule<View>)
    }
}


