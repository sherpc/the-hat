<template id="newPlayer">
    <div class="pure-g">
        <div class="pure-u-1">
            <form class="pure-form">
                <fieldset>
                    <input type="text" v-model="name" placeholder="Твое имя">
                    <button v-on:click.prevent="joinGame" :disabled="!(name.trim())" type="submit" class="pure-button pure-button-primary">Зайти в игру</button>
                </fieldset>
            </form>
        </div>
    </div>
</template>
<script>
    Vue.component("newPlayer", {
        template: "#newPlayer",
        data: () => ({
            name: ''
        }),
        props: {
          gameId: String
        },
        methods: {
            joinGame() {
                const name = this.name && this.name.trim();
                if (!this.name) {
                    return
                }
                this.$emit('joining');
                Vue.http.post(`/api/games/${this.gameId}/join`, name).then(response => {
                    return response.json();
                }).then(gameContext => {
                    this.name = '';
                    this.$emit('game-joined', gameContext);
                });
            }
        }
    });
</script>