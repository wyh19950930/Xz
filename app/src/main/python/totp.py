# coding:utf-8
import hmac
import hashlib
import time
import pickle

'''
    authro:zhangx
    date:20230708
    mark:
        动态口令
'''
DYNAMIC_PASSWORD = "zhangx2023_!070812345678900987654321qazwsxedcrfvtgbyhnujmik,ol.678912344566699944435109637fgvrcdgHHBFHMJHJBFFV"
DIGITS_POWER = [1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000]


def hmac_sha(key, msg, crypto):
    if not key:
        return False
    else:
        key_str = f'{key}{DYNAMIC_PASSWORD}'
        key_str = bytes(key_str, encoding='utf-8')
        msg = pickle.dumps(msg, protocol=3)
        myhmac = hmac.new(key_str, msg, crypto)
        return myhmac.hexdigest()

def generateTOTP(key):
        T0 = 0
        X = 60

        T = int((time.time() - T0) / X)
        steps = str(T).upper()
        while len(steps) < 16:
            steps = "0" + steps

        hash = hmac_sha(key, steps, hashlib.sha512)
        bytes_hash = [hash[i:i + 2] for i in range(0, len(hash), 2)]
        offset = int(bytes_hash[len(bytes_hash) - 1], 16) & 0xf
        binary = ((int(bytes_hash[offset], 16) & 0x7f) << 24) \
                 | ((int(bytes_hash[offset + 1], 16) & 0xff) << 16) \
                 | ((int(bytes_hash[offset + 2], 16) & 0xff) << 8) \
                 | (int(bytes_hash[offset + 3], 16) & 0xff)

        otp = binary % DIGITS_POWER[6]
        result = str(otp)
        while len(result) < 6:
            result = "0" + result
        return result

# seed_sha512 = "SSDD-1123-2323-4332"

# print(generateTOTP(seed_sha512))
