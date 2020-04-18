<template id="timer">
    <div>
        <div>remaining seconds: {{secondsRemaining}}</div>
        <div><button v-on:click="timeoutId ? pause() : start()">{{playPauseButtonText}}</button></div>
    </div>
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
                default: 30,
                validator: function(value) {
                    return value > 0;
                }
            }
        },
        methods: {
            start() {
                this.timeoutId = setInterval(() => {
                    this.secondsRemaining -= 1;
                    if (this.secondsRemaining === 0) {
                        clearInterval(this.timeoutId);
                        this.$emit('timer-finished')
                    }
                }, 1000);
            },
            pause() {
                this.clearTimeoutId();
            },
            clearTimeoutId() {
                if (this.timeoutId) {
                    clearInterval(this.timeoutId);
                    this.timeoutId = null;
                }
            }
        },
        computed: {
            playPauseButtonText() {
                return this.timeoutId ? 'pause' : 'play';
            }
        },
        created() {
            this.secondsRemaining = this.duration;
            this.start();
        },
        beforeDestroy() {
            this.clearTimeoutId();
        }
    });
</script>