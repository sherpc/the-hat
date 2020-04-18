<template id="gameBoard">
    <div>
        <div>
            <h3>Game state</h3>
            game state: {{game.state}}
            <br/>
            game board, hello {{player.name}}!
        </div>
        <div v-if="game.state == 'GatheringParty'"><h4>Awaiting of other players to join.</h4></div>
        <div v-if="game.state == 'Playing'">
            <div>
                <h3>Teams queue</h3>
                <ul>
                    <li v-for="pair in teamsQueue">
                        <strong v-if="pair.active">(*)</strong>
                        {{pair.explainer.name}} → {{pair.listener.name}}
                    </li>
                </ul>
            </div>
            <component v-bind:is="playerState"
                       v-bind:active-pair="activePair"
                       v-bind:initial-deck="game.deck"
                       v-on:remaining-deck="onRemainingDeck"></component>
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
            }
        },
        computed: {
            teamsQueue() {
                return this.game.teams.map((team, i) => {
                    return {
                        explainer: this.playerById(team.explainerId),
                        listener: this.playerById(team.listenerId),
                        active: i == this.game.currentTeam
                    }
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
            }
        }
    });
</script>