import * as both from '@mpetuska/both'
import * as browser from '@mpetuska/browser'
import * as node from '@mpetuska/node'
import * as mpp from '@mpetuska/mpp'

both.sandbox.greet({name: 'Both', sureName: 'Simple'})
browser.sandbox.greet({name: 'Browser', sureName: 'Simple'})
node.sandbox.greet({name: 'Node', sureName: 'Simple'})
mpp.sandbox.greet({name: 'Node', sureName: 'MPP'})
