package io.pmmp.poggit

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*

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

val STANDARD_HEADER = listOf(
	"Java" to System.getProperty("java.version"),
	"Kotlin" to KotlinVersion.CURRENT,
	"ktor" to "1.1.2",
	"Poggit" to "2.0.0"
).joinToString(" ") {"${it.first}/${it.second}"}

fun Application.installHttp() {
	install(Compression) {
		gzip {priority = 1.0}
		deflate {
			priority = 10.0
		}
	}
	install(AutoHeadResponse)
	install(ConditionalHeaders)
	install(XForwardedHeaderSupport)
	install(DefaultHeaders) {
		header("X-Powered-By", STANDARD_HEADER)
	}

	install(CallId) {
		retrieve {it.request.headers["cf-ray-id"]}
		generate(32)
	}
}
