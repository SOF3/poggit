<?php

/*
 * Poggit
 *
 * Copyright (C) 2016-2017 Poggit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace poggit\module;

use poggit\account\Session;
use poggit\utils\internet\Curl;
use poggit\utils\internet\GitHubAPIException;

class GitHubApiProxyAjax extends AjaxModule {
    protected function impl() {
        $url = $this->param("url");
        $post = json_decode($_REQUEST["input"] ?? "{}");
        $method = strtoupper($_REQUEST["method"] ?? "GET");
        $extraHeaders = json_decode($_REQUEST["extraHeaders"] ?? "[]");
        header("Content-Type: application/json");
        $session = Session::getInstance();
        $tk = $session->getAccessToken();
        $session->close();
        try {
            echo json_encode(Curl::ghApiCustom($url, $method, $post, $tk, false, $extraHeaders),
                JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | (($_REQUEST["beautify"] ?? false) ? JSON_PRETTY_PRINT : 0));
        } catch(GitHubAPIException $e) {
            echo json_encode($e, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE | (($_REQUEST["beautify"] ?? false) ? JSON_PRETTY_PRINT : 0));
        }
    }

    public function getName(): string {
        return "proxy.api.gh";
    }
}
