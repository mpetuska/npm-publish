import both from '@dev.petuska/both'
import browser from '@dev.petuska/browser'
import node from '@dev.petuska/node'
import mppBrowser from '@dev.petuska/mpp-browser'
import mppNode from '@dev.petuska/mpp-node'

both.test.sandbox.sayFormalHello({name: 'Both', sureName: 'Simple'})
browser.test.sandbox.sayFormalHello({name: 'Browser', sureName: 'Simple'})
node.test.sandbox.sayFormalHello({name: 'Node', sureName: 'Simple'})
mppBrowser.test.sandbox.sayFormalHello({name: 'Browser', sureName: 'MPP'})
mppNode.test.sandbox.sayFormalHello({name: 'Node', sureName: 'MPP'})