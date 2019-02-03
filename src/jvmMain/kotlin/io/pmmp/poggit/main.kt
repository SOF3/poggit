package io.pmmp.poggit

import io.ktor.application.install
import io.ktor.features.DataConversion
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.pmmp.poggit.gh.auth.installAuth
import io.pmmp.poggit.home.HomeModule
import io.pmmp.poggit.logger.installLogger
import io.pmmp.poggit.session.installSessions

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

fun main() = embeddedServer(Netty, 21080) {
	installLogger()

	installAuth()
	installSessions()

	installHttp()

//	install(ContentNegotiation) {
//		register(ContentType.Text.Html, HtmlContentConverter)
//		register(ContentType.Application.Json, JsonContentConverter)
//	}

	val modules = listOf(HomeModule)
	install(DataConversion) {
	}
	install(Routing) {

	}
}.run {}
