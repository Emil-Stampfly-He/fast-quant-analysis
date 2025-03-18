package org.imperial.fastquantanalysis
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

data class Person(val name: String, val age: Int)

class KotlinDependenciesTest {

    @Test
    fun testJacksonKotlinModule() {
        val mapper = jacksonObjectMapper()
        val person = Person("Emil", 23);
        val json = mapper.writeValueAsString(person)
        val personFromJson = mapper.readValue(json, Person::class.java)
        assertEquals(person, personFromJson)
        assertEquals(person.name, "Emil")
    }

    @Test
    fun testSimpleMainFunction() {
        println("Hello. Kotlin World!")
    }

}