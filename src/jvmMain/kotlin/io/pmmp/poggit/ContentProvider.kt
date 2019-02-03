package io.pmmp.poggit

import com.google.gson.Gson
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.request.contentCharset
import io.ktor.util.pipeline.PipelineContext
import io.pmmp.poggit.api.ApiModel
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.readRemaining
import kotlinx.io.charsets.decode
import java.nio.charset.Charset

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

data class ContentProvider(
	val html: (suspend ()->String)? = null,
	val json: (suspend ()->ApiModel)? = null
)

sealed class MyContentConverter<I> : ContentConverter {
	override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? =
		null // not designed to support receiving

	override suspend fun convertForSend(
		context: PipelineContext<Any, ApplicationCall>,
		contentType: ContentType,
		value: Any
	): Any? {
		if(value !is ContentProvider) return null
		val fn = valueToFn(value) ?: return null
		val result = process(fn())
		return TextContent(result, contentType.withCharset(Charset.defaultCharset()))
	}

	abstract fun valueToFn(value: ContentProvider): (suspend ()->I)?
	abstract fun process(value: I): String
}

object HtmlContentConverter : MyContentConverter<String>() {
	override fun valueToFn(value: ContentProvider) = value.html
	override fun process(value: String) = value
}

object JsonContentConverter : MyContentConverter<ApiModel>() {
	private val gson = Gson()
	override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
		val request = context.subject
		val channel = request.value as? ByteReadChannel ?: return null
		val reader = (context.call.request.contentCharset() ?: Charsets.UTF_8).newDecoder()
			.decode(channel.readRemaining()).reader()
		return gson.fromJson(reader, request.type.javaObjectType)
	}

	override fun valueToFn(value: ContentProvider): (suspend ()->ApiModel)? = value.json
	override fun process(value: ApiModel) = value.toJson()
}
