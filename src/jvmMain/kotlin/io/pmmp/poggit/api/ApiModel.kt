package io.pmmp.poggit.api

import com.google.gson.stream.JsonWriter
import java.io.StringWriter

/*
 * Poggit
 *
 * Copyright(C) 2019 Poggit
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

interface ApiModel {
	fun toJson() = StringWriter().run {
		JsonWriter(this).use {toJson(it)}
		return@run buffer.toString()
	}

	fun toJson(writer: JsonWriter) {
		val fields = this.javaClass.fields
		writer.beginObject()
		for(field in fields) {
			field.getAnnotation(ApiField::class.java) ?: continue
			writer.name(field.name)
			if(field.type.isArray) {
				writer.beginArray()
				for(value in field.get(this) as Array<*>){
					writeValue(writer, value)
				}
				writer.endArray()
			}
		}
		writer.endObject()
	}
}

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun writeValue(writer: JsonWriter, value: Any?): Unit = when(value){
	is ApiModel -> value.toJson(writer)
	is Long -> writer.value(value)
	is Double -> writer.value(value)
	is Boolean -> writer.value(value)
	is Number -> writer.value(value)
	is String -> writer.value(value)
	null -> writer.nullValue()
	else -> throw IllegalArgumentException("Invalid value type")
}.run{}

annotation class ApiField
