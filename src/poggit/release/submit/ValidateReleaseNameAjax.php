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

namespace poggit\release\submit;

use poggit\module\AjaxModule;
use poggit\release\Release;

class ValidateReleaseNameAjax extends AjaxModule {
    public function getName(): string {
        return "release.submit.validate.name";
    }

    protected function impl() {
        $name = $this->param("name");
        $ok = Release::validateName($name, $message);
        echo json_encode(["ok" => $ok, "message" => $message]);
    }
}
