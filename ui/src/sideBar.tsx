import React from 'react';
import {
    Box,
    Flex,
    Heading,
    Text,
    Button,
    useColorModeValue,
    Stack,
    useColorMode,
    Image,
    Icon,
    HStack
} from '@chakra-ui/react';
import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import logo from './rlcs-high-resolution-logo-transparent.svg'

export default function SideBar(): JSX.Element {
    const { colorMode, toggleColorMode } = useColorMode();
    const bannerColour = colorMode === 'light' ? 'linear(to-br, gray.100, gray.300)' : 'linear(to-tl, gray.700, gray.800)';

    return (
        <>
            <Box px={10} bgGradient={bannerColour}>
                <Flex h={14} alignItems='center' justifyContent='space-between'>
                    <Image src={logo} boxSize={16}></Image>
                    <Flex alignItems='center'>
                        <Stack direction='row' spacing={7}>
                            <Button onClick={toggleColorMode} bg={useColorModeValue('grey.500', 'grey.900')}>
                                {colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
                            </Button>
                        </Stack>
                    </Flex>
                </Flex>
            </Box>
        </>
    );
}
