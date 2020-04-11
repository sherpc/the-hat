<template id="game">
    <div>
        <div v-if="game">
            <div>game id / title: {{game.id}} / {{game.settings.title}}</div>
            <div>capacity: {{playersCount}} / {{game.settings.personsCount}}</div>

            <div v-if="player">game board, hello {{player.name}}!</div>

            <div v-else>
                <new-player v-if="availableToJoin" v-bind:game-id="game.id" v-on:game-joined="onGameJoined"></new-player>
                <div v-else>Game already started.</div>
            </div>

        </div>
    </div>
</template>
<script>
    Vue.component("game", {
        template: "#game",
        data: () => ({
            game: null,
            playerId: null
        }),
        created() {
            const gameId = this.$javalin.pathParams["game-id"];
            const gameFromSharedState = this.$javalin.state.game;
            if (gameFromSharedState) {
                this.game = gameFromSharedState;
                const playerIdFromSharedState = this.$javalin.state.playerId;
                if (playerIdFromSharedState) {
                    this.playerId = playerIdFromSharedState;
                }
            } else {
                fetch(`/api/games/${gameId}`)
                    .then(res => res.json())
                    .then(res => this.game = res)
                    .catch(e => console.error("Error while fetching game: ", e));
            }
        },
        computed: {
            playersCount() {
                return Object.keys(this.game.players).length;
            },
            availableToJoin() {
                return this.game.state == 'GatheringParty';
            },
            player() {
                if (this.playerId) {
                    return this.game.players[this.playerId];
                }
                return null;
            }
        },
        methods: {
            onGameJoined: function(gameContext) {
                window.history.pushState(gameContext, "Game joined", window.location.pathname + '/' + gameContext.playerId);
                console.info("Game joined, player: ", gameContext.playerId);
                this.game = gameContext.game;
                this.playerId = gameContext.playerId;
            }
        }
    });
</script>