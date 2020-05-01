<template id="gameAdmin">
    <div class="pure-g">
        <div class="pure-u-1">
            <div v-show="showLoading">...LOADING...</div>
            <div v-if="game">
                <h4 class="is-center">{{game.settings.title}}</h4>

                <table class="pure-table pure-table-horizontal">
                    <thead>
                    <tr>
                        <th><i class="fa fa-users"></i> Имя</th>
                        <th>Статус</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="player in game.players">
                        <td><a :href="`/games/${game.id}/${player.id}`">{{player.name}}</a></td>
                        <td>{{player.state}}</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
    Vue.component("game-admin", {
        template: "#gameAdmin",
        data: () => ({
            game: null,
            loading: true
        }),
        created() {
            const gameId = this.$javalin.pathParams["game-id"];
            fetch(`/api/games/${gameId}`)
                .then(res => res.json())
                .then(res => this.game = res)
                .then(_ => this.loading = false)
                .catch(e => console.error("Error while fetching game: ", e));
        },
        computed: {
        }
    });
</script>