<template id="gameBoard">
    <div>
        <div>
            <h3>Game state</h3>
            game state: {{game.state}}
            <br/>
            game board, hello {{player.name}}!
        </div>
        <div>
            <h3>Teams queue</h3>
            <ul>
                <li v-for="pair in teamsQueue">
                    <bold v-if="pair.active">(*)</bold>
                    {{pair.explainer.name}} → {{pair.listener.name}}
                </li>
            </ul>
        </div>
    </div>
</template>
<script>
    Vue.component("gameBoard", {
        template: "#gameBoard",
        props: {
            game: Object,
            playerId: Object,
        },
        methods: {
            playerById(id) {
                return this.game.players[id];
            }
        },
        computed: {
            teamsQueue() {
                return this.game.teams.map((pair, i) => {
                    return {
                        explainer: this.playerById(pair.first),
                        listener: this.playerById(pair.second),
                        active: i == this.game.currentTeam
                    }
                });
            },
            player() {
                return this.playerById(this.playerId);
            }
        }
    });
</script>