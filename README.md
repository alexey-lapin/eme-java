EME for Java
==========
This is a port of [EME for Go](https://github.com/rfjakob/eme).

**EME** (ECB-Mix-ECB or, clearer, **Encrypt-Mix-Encrypt**) is a wide-block
encryption mode developed by Halevi
and Rogaway in 2003.

EME uses multiple invocations of a block cipher to construct a new
cipher of bigger block size (in multiples of 16 bytes, up to 2048 bytes).

Quoting from the original paper:

> We describe a block-cipher mode of operation, EME, that turns an n-bit block cipher into
> a tweakable enciphering scheme that acts on strings of mn bits, where m ∈ [1..n]. The mode is
> parallelizable, but as serial-efficient as the non-parallelizable mode CMC [6]. EME can be used
> to solve the disk-sector encryption problem. The algorithm entails two layers of ECB encryption
> and a “lightweight mixing” in between. We prove EME secure, in the reduction-based sense of
> modern cryptography.

This is an implementation of EME in Java, complete with test vectors from IEEE
and Halevi.

It has no dependencies outside the standard library.