<template id="game">
    <div class="pure-g">
        <div class="pure-u-1">
            <div v-bind:style="{ display: showLoading }">...LOADING...</div>
            <div v-bind:style="{ display: showGameBoard }">
                <div v-if="game">
                    <h4 class="is-center">
                        <button v-if="online != null" disabled style="opacity: 1" class="button-xsmall pure-button"
                                v-bind:class="{ 'button-success': online, 'button-error': !online }">
                            <i class="fa fa-plug" aria-hidden="true"></i>
                        </button>
                        {{game.settings.title}} (раунд: {{roundDescription}})
                    </h4>

                    <div v-if="player">
                        <selecting-words
                                v-if="player.state == 'SelectingWords'"
                                v-bind:words-count="game.settings.wordsCount"
                                v-bind:game-id="game.id"
                                v-bind:player="player">
                        </selecting-words>
                        <game-board
                                v-if="player.state == 'ReadyToPlay'"
                                v-bind:game="game"
                                v-bind:player-id="player.id"></game-board>
                    </div>

                    <div v-else>
                        <new-player v-if="availableToJoin"
                                    v-bind:game-id="game.id"
                                    v-on:game-joined="onGameJoined"></new-player>
                        <div v-else>Игра уже началась :( <br/> Создай новую на главной!</div>
                    </div>
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
            loading: false,
            online: null
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
            availableToJoin() {
                return this.game.state == 'GatheringParty' && this.playersCount < this.game.settings.playersCount;
            },
            playersCount() {
                return Object.keys(this.game.players).length;
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
            },
            roundDescription() {
                return this.game && this.game.round && t['round'][this.game.round];
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
                const wsProtocol = location.protocol !== 'https:' ? 'ws' : 'wss';
                let url = `${wsProtocol}://${location.hostname}:${location.port}/${path}`;
                let ws = new WebSocket(url);
                ws.onmessage = msg => this.updateGameState(JSON.parse(msg.data));
                ws.onclose = () => {
                    this.online = false;
                    console.warn("WebSocket connection closed");
                };
                ws.onopen = () => {
                    this.online = true;
                    console.info("WebSocket connected.");
                };
                this.setTitle();
            },
            updateGameState(gameContext) {
                this.game = gameContext.game;
                this.playerId = gameContext.playerId;
            },
            setTitle() {
                if (this.player)
                    document.title = 'The Hat | ' + this.player.name;
            }
        }
    });
</script>