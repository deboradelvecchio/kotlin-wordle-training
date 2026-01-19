package com.doctolib.kotlinwordletraining.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

/** Controller serving the welcome page without authentication. */
@Controller
class SimpleController(
    @Value("\${welcome.message:Welcome to your awesome new Doctoboot service!}")
    private val message: String
) {

    @GetMapping("/")
    @PreAuthorize("permitAll()")
    fun show(model: Model): String {
        model.addAttribute("serviceName", "kotlin-wordle-training")
        model.addAttribute("message", message)
        return "index"
    }
}
