<template id="games">
    <div>
        <ul class="user-overview-list">
            <li v-for="game in games">
                <a :href="`/games/${game.id}`">{{game.settings.title}} ({{game.settings.wordsCount}} words, {{game.settings.personsCount}} persons)</a>
            </li>
        </ul>
    </div>
</template>
<script>
    Vue.component("games", {
        template: "#games",
        data: () => ({
            games: []
        }),
        created() {
            fetch("/api/games")
                .then(res => res.json())
                .then(res => this.games = res)
                .catch(e => console.error("Error while fetching games: ", e))
        }
    });
</script>
<style>
    ul.user-overview-list {
        padding: 0;
        list-style: none;
    }
    ul.user-overview-list a {
        display: block;
        padding: 16px;
        border-bottom: 1px solid #ddd;
    }
    ul.user-overview-list a:hover {
        background: #00000010;
    }
</style>