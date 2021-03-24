<template>
  <elFrame :src="formApi" />
</template>
<script>
import { mapGetters } from 'vuex'
import elFrame from '@/components/Iframe/index'
export default {
  name: 'Form',
  components: { elFrame },
  computed: {
    ...mapGetters([
      'formApi'
    ])
  }
}
</script>
