<template id="newUser">
    <div>
        <input v-model="name" placeholder="Your name">
        <button v-on:click="joinGame">Join game</button>
    </div>
</template>
<script>
    Vue.component("newUser", {
        template: "#newUser",
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
                    const user = response.json();
                    console.info("Game joined, user: ", user);
                    this.name = '';
                });
            }
        }
    });
</script>