<template id="gameBoard">
    <div>
        <div v-if="game.state == 'GatheringParty'">
            <div class="pure-g">
                <div class="pure-u-1">Ждем, пока все соберутся и введут слова. <br/> Собралось {{playersCount}} из {{game.settings.playersCount}} игроков.</div>
            </div>
        </div>
        <div v-if="game.state == 'Playing'">
            <div class="pure-g">
                <div class="pure-u-1" v-bind:class="playerIsActive ? '' : 'pure-u-lg-1-2'">
                    <component v-bind:is="playerState"
                               v-bind:active-pair="activePair"
                               v-bind:initial-deck="game.deck"
                               v-bind:explain-timeout-seconds="game.settings.explainTimeoutSeconds"
                               v-on:remaining-deck="onRemainingDeck"
                               v-on:next-team="onNextTeam"></component>
                </div>
                <div v-if="!playerIsActive" class="pure-u-1 pure-u-lg-1-2">
                    <h4 class="is-center">Очередность команд</h4>
                    <ul>
                        <li v-for="team in teamsQueue">
                            <i v-if="team.active" class="fa fa-play"></i>
                            ({{team.score}}) {{team.explainer.name}} → {{team.listener.name}}
                        </li>
                    </ul>
                </div>
            </div>
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
            onNextTeam() {
                Vue.http.post(`/api/games/${this.game.id}/${this.playerId}/nextTeam`).then(response => {
                    return true;
                }, err => console.error(err));
            }
        },
        computed: {
            playersCount() {
                return Object.keys(this.game.players).length;
            },
            teamsQueue() {
                return this.game.teams.map((team, i) => {
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
                return this.playerState != 'observer';
            }
        }
    });
</script>