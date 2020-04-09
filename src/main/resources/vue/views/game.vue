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
            player: null
        }),
        created() {
            const gameId = this.$javalin.pathParams["game-id"];
            const gameFromSharedState = this.$javalin.state.game;
            if (gameFromSharedState) {
                this.game = gameFromSharedState;
                const playerFromSharedState = this.$javalin.state.player;
                if (playerFromSharedState) {
                    this.player = playerFromSharedState;
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
            }
        },
        methods: {
            onGameJoined: function(player) {
                window.history.pushState(player, "Game joined", window.location.pathname + '/' + player.id);
                console.info("Game joined, player: ", player);
                this.player = player;
            }
        }
    });
</script>