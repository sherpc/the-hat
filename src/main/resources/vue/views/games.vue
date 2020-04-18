<template id="games">
    <div>
        <div class="pure-g">
            <div class="pure-u-1-2">
                <form class="pure-form">
                    <fieldset>
                        <legend>New game</legend>
                        <input class="pure-input-1-4" v-model="newGame.title" placeholder="New game name">
                        <input style="width: 50px" type="number" v-model.number="newGame.wordsCount" placeholder="Words count">
                        <input style="width: 50px" type="number" v-model.number="newGame.playersCount" placeholder="Players count">

                        <button v-on:click="createGame" type="submit" class="pure-button pure-button-primary">Create new game</button>
                    </fieldset>
                </form>
            </div>
        </div>
        <hr/>
        <div class="pure-g">
            <div class="pure-u-1">
                <table class="pure-table pure-table-horizontal">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Words</th>
                            <th>Players</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="game in games">
                            <td><a :href="`/games/${game.id}`">{{game.settings.title}}</a></td>
                            <td>{{game.settings.wordsCount}}</td>
                            <td>{{game.settings.playersCount}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<script>
    const newGameTemplate = function() {
        return {
            title: "",
            wordsCount: 5,
            playersCount: 6
        };
    };

    Vue.component("games", {
        template: "#games",
        data: () => ({
            games: [],
            newGame: newGameTemplate()
        }),
        created() {
            this.fetchGames();
        },
        methods: {
            createGame() {
                this.newGame.title = this.newGame.title && this.newGame.title.trim();
                var newGame = this.newGame;
                if (!newGame){
                    return
                }

                Vue.http.post("/api/games/", this.newGame).then(() => {
                    console.info("New game created");
                    this.fetchGames();
                    this.newGame = newGameTemplate;
                });
            },
            fetchGames() {
                fetch("/api/games")
                    .then(res => res.json())
                    .then(res => this.games = res)
                    .catch(e => console.error("Error while fetching games: ", e))
            }
        }
    });
</script>
<style>

</style>