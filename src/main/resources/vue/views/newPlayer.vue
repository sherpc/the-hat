<template id="newPlayer">
    <div>
        <input v-model="name" placeholder="Your name">
        <button v-on:click="joinGame">Join game</button>
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