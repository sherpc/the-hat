<template id="gameBoard">
    <div>
        <div v-if="game.state == 'GatheringParty'">
            <div class="pure-g">
                <div class="pure-u-1">Ждем, пока все соберутся и введут слова. <br/> Собралось {{playersCount}} из {{game.settings.playersCount}} игроков.</div>
            </div>
        </div>
        <div v-if="game.state == 'Finished'">
            <h3 class="is-center">Игра окончена!</h3>
        </div>
        <div v-if="game.state != 'GatheringParty' && !playerIsActive">
            <div class="pure-g">
                <div class="pure-u-1">
                    <h4 class="is-center">Статистика (за последний ход объяснено <strong>{{lastExplainerScore}}</strong>)</h4>
                </div>
            </div>
            <div class="pure-g">
                <div class="pure-u-1" style="overflow-x: scroll">
                    <table class="pure-table pure-table-bordered">
                        <thead>
                        <tr>
                            <th><i class="fa fa-users"></i> Команда</th>
                            <th class="round" v-for="round in rounds">
                                {{roundTranslation(round)}}
                            </th>
                            <th>Всего</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-bind:class="{ leader: team.isLeader }" v-for="team in teamsWithStats">
                            <td>
                                <span v-if="team.isLeader">🎉</span> {{team.name}}
                            </td>
                            <td class="is-center" v-for="round in rounds">
                                {{team.scores[round]}}
                            </td>
                            <td class="is-center"><span>{{team.totalScore}}</span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <div v-if="game.state == 'Playing'">
            <div class="pure-g">
                <div class="pure-u-1" v-bind:class="playerIsActive ? '' : 'pure-u-lg-1-2'">
                    <component v-bind:is="playerState"
                               v-bind:active-pair="activePair"
                               v-bind:initial-deck="game.deck"
                               v-bind:explain-timeout-seconds="game.settings.explainTimeoutSeconds"
                               v-on:word-explained="onWordExplained"
                               v-on:next-team="onNextTeam"></component>
                </div>
                <div v-if="!playerIsActive" class="pure-u-1 pure-u-lg-1-2">
                    <h4 class="is-center">Очередность команд</h4>
                    <ul>
                        <li v-for="team in teamsQueue">
                            <i v-if="team.active" class="fa fa-play"></i>
                            {{team.explainer.name}} → {{team.listener.name}}
                        </li>
                    </ul>
                </div>
            </div>
            <deck-progress
                    v-if="!playerIsActive"
                    v-bind:deck-size="initialDeckSize"
                    v-bind:explained-words="explainedWordsInRound">
            </deck-progress>
        </div>
    </div>
</template>
<script>
    Vue.component("gameBoard", {
        template: "#gameBoard",
        props: {
            game: Object,
            playerId: String,
        },
        methods: {
            playerById(id) {
                return this.game.players[id];
            },
            onRemainingDeck(remainingDeck) {
                Vue.http.post(`/api/games/${this.game.id}/${this.playerId}/remainingDeck`, remainingDeck).then(response => {
                    return true;
                }, err => console.error(err));
            },
            onWordExplained(word) {
                Vue.http.post(`/api/games/${this.game.id}/${this.playerId}/wordExplained`, word).then(response => {
                    return true;
                }, err => console.error(err));
            },
            onNextTeam() {
                Vue.http.post(`/api/games/${this.game.id}/${this.playerId}/nextTeam`).then(response => {
                    return true;
                }, err => console.error(err));
            },
            roundTranslation(round) {
                return t['round'][round];
            }
        },
        computed: {
            playersCount() {
                return Object.keys(this.game.players).length;
            },
            teamsQueue() {
                return this.game.pairs.map((team, i) => {
                    let t = {
                        explainer: this.playerById(team.explainerId),
                        listener: this.playerById(team.listenerId),
                        active: i == this.game.currentTeam,
                    };
                    t.score = t.explainer.score + t.listener.score;
                    return t;
                });
            },
            activePair() {
                return this.teamsQueue.filter(p => p.active)[0];
            },
            player() {
                return this.playerById(this.playerId);
            },
            playerState() {
                if (this.activePair.explainer.id == this.playerId)
                    return 'explainer';
                else if (this.activePair.listener.id == this.playerId)
                    return 'listener-player';
                else
                    return 'observer';
            },
            playerIsActive() {
                return this.playerState != 'observer' && this.game.state == 'Playing';
            },
            rounds() {
                return this.game ? Object.keys(this.game.scores) : [];
            },
            teamsWithStats() {
                const game = this.game;
                const rounds = this.rounds;
                let result = game.teams.map(t => {
                    let scores = rounds.map(round => {
                        let roundScore = game.scores[round];
                        let roundTeamScore = (roundScore[t.firstPlayerId] || 0) + (roundScore[t.secondPlayerId] || 0);
                        return [round, roundTeamScore];
                    });
                    return {
                        name: this.playerById(t.firstPlayerId).name + ' + ' + this.playerById(t.secondPlayerId).name,
                        scores: Object.fromEntries(scores),
                        totalScore: scores.map(s => s[1]).reduce((acc, x) => acc + x, 0)
                    };
                });

                const maxScore = result.map(t => t.totalScore).reduce((acc, x) => x > acc ? x : acc, 0);

                return result.map(t => {
                    t.isLeader = t.totalScore == maxScore;
                    return t;
                });
            },
            explainedWordsInRound() {
                const game = this.game;
                const round = this.game.round;

                let roundScore = game.scores[round];
                return Object.values(roundScore).reduce((acc, x) => acc + x, 0);
            },
            initialDeckSize() {
                return this.game?.initialDeckSize || 0;
            },
            lastExplainerScore() {
                return this.game?.lastExplainerScore.previousScore || 0;
            }
        }
    });
</script>