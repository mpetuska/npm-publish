import * as both from '@mpetuska/both'
import * as browser from '@mpetuska/browser'
import * as node from '@mpetuska/node'

both.sandbox.greet({name: 'Both', sureName: 'Simple'})
browser.sandbox.greet({name: 'Browser', sureName: 'Simple'})
node.sandbox.greet({name: 'Node', sureName: 'Simple'})
