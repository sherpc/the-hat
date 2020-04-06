<template id="game">
    <div>
        <ul v-if="game">
            <dt>game ID</dt>
            <dd>{{game.id}}</dd>
            <dt>Title</dt>
            <dd>{{game.settings.title}}</dd>
            <dt>Words count</dt>
            <dd>{{game.settings.wordsCount}}</dd>
            <dt>Persons count</dt>
            <dd>{{game.settings.personsCount}}</dd>
        </ul>
    </div>
</template>
<script>
    Vue.component("game", {
        template: "#game",
        data: () => ({
            game: null,
        }),
        created() {
            const gameId = this.$javalin.pathParams["game-id"];
            fetch(`/api/games/${gameId}`)
                .then(res => res.json())
                .then(res => this.game = res)
                .catch(e => console.error("Error while fetching game: ", e));
        }
    });
</script>