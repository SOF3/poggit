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

namespace poggit\ci\ui;

use poggit\account\Session;
use poggit\Meta;

class SelfBuildPage extends RepoListBuildPage {
    public function __construct() {
        if(!Session::getInstance()->isLoggedIn()) {
            throw new RecentBuildPage;
        }
        parent::__construct();
    }

    public function getTitle(): string {
        return "My Projects";
    }

    public function output() {
        ?>
        <div class="memberciwrapper">
            <div class="togglepane">
                <div class="repolist">
                    <p class="remark">Organization repos not showing up?<br/><a
                                href="<?= Meta::root() ?>orgperms">Check organization access on GitHub</a></p>
                    <div id="toggle-orgs"></div>
                    <div id="enableRepoBuilds">
                        <div id="detailLoader"></div>
                    </div>
                </div>
            </div>
            <div class="repopane">
                <div class="ajaxpane"></div>
                <?php
                if(count($this->repos) > 0) {
                    $this->displayRepos($this->repos);
                } else { ?>
                    <p>You don't have any projects built by Poggit-CI yet! Enable a repo in the repo list above/on the
                        left, click the "off" button to enable the repo, and create a .poggit.yml according to the
                        instructions. If you already have a .poggit.yml, push a commit that modifies .poggit.yml (e.g.
                        add a new trailing line) to trigger Poggit-CI to build for the first time.</p>
                <?php } ?>
                <script>
                    <?php
                    $enabledRepos = [];
                    foreach($this->repos as $repo) {
                        $enabledRepos[$repo->id] = [
                            "owner" => $repo->owner->login,
                            "name" => $repo->name,
                            "projectsCount" => count($repo->projects),
                            "id" => $repo->id
                        ];
                    }
                    ?>
                    briefEnabledRepos = <?= json_encode($enabledRepos, JSON_UNESCAPED_SLASHES) ?>;
                </script>
            </div>
        </div>
        <?php
    }

    /**
     * @return \stdClass[]
     */
    protected function getRepos(): array {
        $rawRepos = $this->getReposByGhApi("user/repos?per_page=" . Meta::getCurlPerPage(), Session::getInstance()->getAccessToken());
        return $rawRepos;
    }

    protected function throwNoRepos() {
    }

    protected function throwNoProjects() {
    }
}
