import {sandbox} from '@mpetuska/sandbox'
import * as both from '@mpetuska/both'
import * as browser from '@mpetuska/browser'
import * as node from '@mpetuska/node'
import * as mppBrowser from '@mpetuska/mpp-browser'
import * as mppNode from '@mpetuska/mpp-node'

sandbox.sayWelcome()
both.test.sandbox.sayFormalHello({name: 'Both', sureName: 'Simple'})
browser.test.sandbox.sayFormalHello({name: 'Browser', sureName: 'Simple'})
node.test.sandbox.sayFormalHello({name: 'Node', sureName: 'Simple'})
mppBrowser.test.sandbox.sayFormalHello({name: 'Browser', sureName: 'MPP'})
mppNode.test.sandbox.sayFormalHello({name: 'Node', sureName: 'MPP'})