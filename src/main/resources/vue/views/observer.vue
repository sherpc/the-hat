<template id="observer">
    <div>
        <h4>You are observer</h4>
        Now <i>{{activePair.explainer.name}}</i> explains words to <i>{{activePair.listener.name}}</i>.
    </div>
</template>
<script>
    Vue.component("observer", {
        template: "#observer",
        props: {
            activePair: Object
        }
    });
</script>