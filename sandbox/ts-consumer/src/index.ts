import * as both from '@mpetuska/both'
import * as browser from '@mpetuska/browser'
import * as node from '@mpetuska/node'
import * as mppBrowser from '@mpetuska/mpp-browser'
import * as mppNode from '@mpetuska/mpp-node'

both.sandbox.greet({name: 'Both', sureName: 'Simple'})
browser.sandbox.greet({name: 'Browser', sureName: 'Simple'})
node.sandbox.greet({name: 'Node', sureName: 'Simple'})
mppBrowser.sandbox.greet({name: 'Browser', sureName: 'MPP'})
mppNode.sandbox.greet({name: 'Node', sureName: 'MPP'})
