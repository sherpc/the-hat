<template id="timer">
    <form class="pure-form pure-form-stacked">
        <fieldset>
            <div class="pure-g">
                <div class="pure-u-1-2">
                    <button type="submit" class="pure-button button-warning" style="margin: 0" v-on:click.prevent="timeoutId ? pause() : start()">
                        <i class="fa" v-bind:class="playPauseClass"></i>
                    </button>
                </div>
                <div class="pure-u-1-2">
                    <label class="seconds-remaining is-center">Осталось сек: {{secondsRemaining}}</label>
                </div>
            </div>
        </fieldset>
    </form>
</template>
<script>
    Vue.component("timer", {
        template: "#timer",
        data: () => ({
            secondsRemaining: 0,
            timeoutId: null,
        }),
        props: {
            duration: {
                type: Number,
                required: true,
                validator: function(value) {
                    return value > 0;
                }
            }
        },
        methods: {
            start() {
                this.timeoutId = setInterval(() => {
                    this.secondsRemaining -= 1;
                    if (this.secondsRemaining === 1) {
                        clearInterval(this.timeoutId);
                        const sound = this.sound;
                        const component = this;
                        sound.play()
                            .then(_ => {
                                sound.addEventListener('ended', __ => component.finish());
                            })
                            .catch(_ => component.finish());
                    }
                }, 1000);
            },
            pause() {
                this.clearTimeoutId();
            },
            finish() {
                this.secondsRemaining -= 1;
                this.$emit('timer-finished')
            },
            clearTimeoutId() {
                if (this.timeoutId) {
                    clearInterval(this.timeoutId);
                    this.timeoutId = null;
                }
            }
        },
        computed: {
            playPauseClass() {
                return this.timeoutId ? 'fa-pause' : 'fa-play';
            }
        },
        created() {
            this.secondsRemaining = this.duration;
            this.sound = new Audio("/time.wav"); // buffers automatically when created
            this.start();
        },
        beforeDestroy() {
            this.clearTimeoutId();
        }
    });
</script>
<style>
    .seconds-remaining {
        font-size: 20px;
    }
</style>