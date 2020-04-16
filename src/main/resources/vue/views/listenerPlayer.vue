<template id="listenerPlayer">
    <div>
        <h4>You are listenerPlayer</h4>
        Now <i>{{activePair.explainer.name}}</i> explains words to you.
    </div>
</template>
<script>
    Vue.component("listenerPlayer", {
        template: "#listenerPlayer",
        props: {
            activePair: Object
        }
    });
</script>