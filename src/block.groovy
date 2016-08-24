@Grapes([
        @Grab('org.bitcoinj:bitcoinj-core:0.14.3'),
        @Grab('org.slf4j:slf4j-simple:1.7.21')
])
import org.bitcoinj.core.Block
import org.bitcoinj.core.Context
import org.bitcoinj.core.Message
import org.bitcoinj.params.UnitTestParams

def context = new Context(UnitTestParams.get())
def blockhex = '''\
f9beb4d91d0100000100000000000000000000000000000000000000000000000000000000000000000000003ba3edfd7a7b12b27ac72c3e67768f6\
17fc81bc3888a51323a9fb8aa4b1e5e4a29ab5f49ffff001d1dac2b7c01010000000100000000000000000000000000000000000000000000000000\
00000000000000ffffffff4d04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696\
e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73ffffffff0100f2052a01000000434104678afdb0fe5548271967f1a67130\
b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5fac00000000'''
    .decodeHex()
def block = new Block(context.params, blockhex, 8, context.params.defaultSerializer, Message.UNKNOWN_LENGTH)
def chunkData = block.transactions[0].inputs[0].scriptSig.chunks[2].data
println new String(chunkData)
