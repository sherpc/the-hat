<template id="explainer">
    <div>
        <h4>You are explainer</h4>
        Now you explains words to <i>{{activePair.listener.name}}</i>.
    </div>
</template>
<script>
    Vue.component("explainer", {
        template: "#explainer",
        props: {
            activePair: Object
        }
    });
</script>