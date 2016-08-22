import org.bitcoinj.core.Context
@Grapes([
        @Grab('org.bitcoinj:bitcoinj-core:0.14.3'),
        @Grab('org.slf4j:slf4j-simple:1.7.21')
])
import org.bitcoinj.core.Sha256Hash
import org.bitcoinj.core.Transaction
import org.bitcoinj.core.Utils
import org.bitcoinj.core.VerificationException
import org.bitcoinj.params.UnitTestParams
import org.bitcoinj.script.Script
import org.bitcoinj.script.ScriptBuilder
import org.bitcoinj.script.ScriptChunk
import org.spongycastle.crypto.digests.RIPEMD160Digest

import static org.bitcoinj.script.ScriptOpCodes.*

def context = new Context(UnitTestParams.get())

// First, make a blank, fake tx to use for the script interpreter. None of the
// scripts we will make will require an actual transaction, because we will not
// be using OP_CHECKSIG or OP_CHECKMULTISIG. So long as we don't use those
// operations, which hash the transaction, the transaction can be blank.
def tx = new Transaction(context.params)

// Example scripts. Note that while most of the scripts evaluate to true, some
// deliberately evaluate to false.
def script1sig = new Script(OP_1 as byte)
def script1pubkey = new Script();
def verify1 = verify(script1sig, tx, script1pubkey)
println "1."
println "   script sig: $script1sig"
println "script pubkey: $script1pubkey"
println "        valid? $verify1"
println()

def script2sig = new ScriptBuilder()
        .op(OP_1)
        .op(OP_1)
        .build()
def script2pubkey = new Script();
def verify2 = verify(script2sig, tx, script2pubkey)
println "2."
println "   script sig: $script2sig"
println "script pubkey: $script2pubkey"
println "        valid? $verify2"
println()

def script3sig = new Script(OP_1 as byte)
def script3pubkey = new Script(OP_1 as byte)
def verify3 = verify(script3sig, tx, script3pubkey)
println "3."
println "   script sig: $script3sig"
println "script pubkey: $script3pubkey"
println "        valid? $verify3"
println()

def script4sig = new Script(OP_0 as byte)
def script4pubkey = new Script(OP_0 as byte)
def verify4 = verify(script4sig, tx, script1pubkey)
println "4."
println "   script sig: ${script4sig}"
println "script pubkey: $script4pubkey"
println "        valid? $verify4"
println()

// TODO work on script5 - 29

def script30sig = new ScriptBuilder()
        .data([0x00] as byte[])
        .data([0x0, 0x0] as byte[])
        .op(OP_NUMEQUAL)
        .build()
def script30pubkey = new Script()
def verify30 = verify(script30sig, tx, script30pubkey)
println('30.')
println "   script sig: $script30sig"
println "script pubkey: $script30pubkey"
println "        valid? $verify30"
println()

def script31sig = new ScriptBuilder()
        .op(OP_1)
        .data('01000000'.decodeHex())
        .op(OP_EQUAL)
        .build()
def script31pubkey = new Script()
def verify31 = verify(script31sig, tx, script31pubkey)
println('31.')
println "   script sig: $script31sig"
println "script pubkey: $script31pubkey"
println "        valid? $verify31"
println()

def script32sig = new ScriptBuilder()
        .op(OP_1)
        .addChunk(new ScriptChunk(1, [0x01] as byte[]))
        .op(OP_EQUAL)
        .build()
def script32pubkey = new Script()
def verify32 = verify(script32sig, tx, script32pubkey)
println('32.')
println "   script sig: $script32sig"
println "script pubkey: $script32pubkey"
println "        valid? $verify32"
println()

def script33sig = new ScriptBuilder()
        .addChunk(new ScriptChunk(OP_0))
        .op(OP_SHA256)
        .data(Sha256Hash.of([] as byte[]).bytes)
        .op(OP_EQUAL)
        .build()
def verify33 = verify(script33sig, tx, script33pubkey)
println('33.')
println "   script sig: $script33sig"
println "script pubkey: $script33pubkey"
println "        valid? $verify33"
println()

def script34sig = new ScriptBuilder()
        .addChunk(new ScriptChunk(OP_0))
        .op(OP_RIPEMD160)
        .data(ripemd160([] as byte[]))
        .op(OP_EQUAL)
        .build()
def script34pubkey = new Script()
def verify34 = verify(script34sig, tx, script34pubkey)
println('34.')
println "   script sig: $script34sig"
println "script pubkey: $script34pubkey"
println "        valid? $verify34"
println()

def script35sig = new ScriptBuilder()
        .addChunk(new ScriptChunk(OP_0))
        .op(OP_HASH160)
        .data(Utils.sha256hash160([] as byte[]))
        .op(OP_EQUAL)
        .build()
def script35pubkey = new Script()
def verify35 = verify(script35sig, tx, script35pubkey)
println('35.')
println "   script sig: $script35sig"
println "script pubkey: $script35pubkey"
println "        valid? $verify35"
println()

def script36sig = new ScriptBuilder()
        .addChunk(new ScriptChunk(OP_0))
        .op(OP_HASH256)
        .data(Sha256Hash.twiceOf([] as byte[]).bytes)
        .op(OP_EQUAL)
        .build()
def script36pubkey = new Script()
def verify36 = verify(script36sig, tx, script36pubkey)
println('36.')
println "   script sig: $script36sig"
println "script pubkey: $script36pubkey"
println "        valid? $verify36"
println()

def redeemScript37 = new ScriptBuilder()
        .op(OP_1)
        .build()
def script37sig = new ScriptBuilder()
        .data(redeemScript37.program)
        .build()
def script37pubkey = new ScriptBuilder().createP2SHOutputScript(redeemScript37)
def verify37 = verify(script37sig, tx, script37pubkey)
println('37.')
println "redeem script: $redeemScript37"
println "   script sig: $script37sig"
println "script pubkey: $script37pubkey"
println "        valid? $verify37"
println()


private static boolean verify(Script scriptSig, Transaction tx, Script scriptPubkey) {
    def verified = true;
    try {
        scriptSig.correctlySpends(tx, 0, scriptPubkey, [Script.VerifyFlag.P2SH] as Set)
    } catch (VerificationException ignored) {
        verified = false;
    }
    return verified
}

static def ripemd160(byte[] dataToHash) {
    RIPEMD160Digest digest = new RIPEMD160Digest();
    digest.update(dataToHash, 0, dataToHash.length);
    byte[] ripmemdHash = new byte[20];
    digest.doFinal(ripmemdHash, 0);
    return ripmemdHash;
}