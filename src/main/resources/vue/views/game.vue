<template id="game">
    <div>
        <div v-bind:style="{ display: showLoading }">...LOADING...</div>
        <div v-bind:style="{ display: showGameBoard }">
            <div v-if="game">
                <div>game id / title: {{game.id}} / {{game.settings.title}}</div>
                <div>capacity: {{playersCount}} / {{game.settings.personsCount}}</div>

                <div v-if="player">game board, hello {{player.name}}!</div>

                <div v-else>
                    <new-player v-if="availableToJoin"
                                v-bind:game-id="game.id"
                                v-on:game-joined="onGameJoined"></new-player>
                    <div v-else>Game already started.</div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    Vue.component("game", {
        template: "#game",
        data: () => ({
            game: null,
            playerId: null,
            loading: false
        }),
        created() {
            const gameId = this.$javalin.pathParams["game-id"];
            const gameContextFromSharedState = this.$javalin.state.gameContext;
            if (gameContextFromSharedState) {
                const gameFromSharedState = gameContextFromSharedState.game;
                if (gameFromSharedState) {
                    this.game = gameFromSharedState;
                    const playerIdFromSharedState = gameContextFromSharedState.playerId;
                    if (playerIdFromSharedState) {
                        this.playerId = playerIdFromSharedState;
                        this.initWsConnection(gameContextFromSharedState);
                    }
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
            showLoading() {
                return this.loading ? 'block' : 'none';
            },
            showGameBoard() {
                return this.loading ? 'none' : 'block';
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
                console.info("Game joined, player:", gameContext.playerId);
                this.updateGameState(gameContext);
                this.initWsConnection(gameContext);
            },
            waiting() {
                this.loading = true;
            },
            initWsConnection(gameContext) {
                console.info('Connecting to WebSocket...');
                let path = ['games', gameContext.game.id, gameContext.playerId].join('/');
                let url = `ws://${location.hostname}:${location.port}/${path}`;
                let ws = new WebSocket(url);
                ws.onmessage = msg => this.updateGameState(JSON.parse(msg.data));
                ws.onclose = () => console.warn("WebSocket connection closed");
                ws.onopen = () => console.info("WebSocket connected.");
            },
            updateGameState(gameContext) {
                this.game = gameContext.game;
                this.playerId = gameContext.playerId;
            }
        }
    });
</script>