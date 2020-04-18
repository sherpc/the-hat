<template id="timer">
    <div>
        remaining seconds: {{secondsRemaining}}
    </div>
</template>
<script>
    Vue.component("timer", {
        template: "#timer",
        data: () => ({
            secondsRemaining: 0,
            timeoutId: null
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
        created() {
            this.secondsRemaining = this.duration;
            this.timeoutId = setInterval(() => {
                this.secondsRemaining -= 1;
                if (this.secondsRemaining === 0) {
                    clearInterval(this.timeoutId);
                    this.$emit('timer-finished')
                }
            }, 1000);
        },
        beforeDestroy() {
            if (this.timeoutId) {
                clearInterval(this.timeoutId);
            }
        }
    });
</script>